"use client";

import { useState, useCallback, useEffect } from "react";
import { Sidebar } from "@/components/sidebar";
import { ChatWindow } from "@/components/chat-window";
import { ProtectedRoute } from "@/components/auth/protected-route";
import type { Conversation, HistoryConversation } from "@/types/chat";
import { useAuth } from "@/contexts/auth-context";
import { chatAPI } from "@/lib/chat-api";
import { generateUniqueId } from "@/lib/utils";

// 主页组件，包含聊天界面和侧边栏
function HomePage() {
  const [conversations, setConversations] = useState<Conversation[]>([]);
  const [activeConversationId, setActiveConversationId] = useState<
    string | undefined
  >();
  const [isLoadingHistory, setIsLoadingHistory] = useState(false);
  const { user, token } = useAuth();

  /**
   * 加载用户历史对话
   */
  const loadUserHistory = useCallback(async () => {
    if (!user?.id || !token) return;

    try {
      setIsLoadingHistory(true);
      const historyConversations = await chatAPI.getUserConversations(
        user.id,
        token
      );

      console.log("Loaded history conversations:", historyConversations);

      // 转换历史对话数据
      const convertedConversations: Conversation[] = historyConversations.map(
        (history: HistoryConversation) => {
          // 添加类型注解
          // 使用 sessionId 作为对话ID，如果没有 sessionId 则使用历史记录ID
          const conversationId = history.sessionId || history.id;

          return {
            id: conversationId,
            title: history.question
              ? history.question.substring(0, 30) +
                (history.question.length > 30 ? "..." : "")
              : "历史对话",
            messages: [
              {
                id: `${history.id}_user`,
                role: "user" as const,
                content: history.question || "",
                timestamp: new Date(history.createTime || Date.now()),
              },
              {
                id: `${history.id}_assistant`,
                role: "assistant" as const,
                content: history.answer || "",
                timestamp: new Date(history.createTime || Date.now()),
              },
            ],
            createdAt: new Date(history.createTime || Date.now()),
            updatedAt: new Date(history.createTime || Date.now()),
          };
        }
      );

      setConversations((prev) => {
        // 使用 Map 来确保 ID 唯一性，后出现的对话会覆盖先出现的
        const conversationMap = new Map<string, Conversation>();

        // 先添加现有的对话
        prev.forEach((conv) => conversationMap.set(conv.id, conv));

        // 添加历史对话，如果有重复 ID 会覆盖
        convertedConversations.forEach((conv) =>
          conversationMap.set(conv.id, conv)
        );

        // 转换为数组并按更新时间排序
        const uniqueConversations = Array.from(conversationMap.values()).sort(
          (a, b) => b.updatedAt.getTime() - a.updatedAt.getTime()
        );

        console.log("Merged conversations:", uniqueConversations);
        return uniqueConversations;
      });
    } catch (error) {
      console.error("Failed to load conversation history:", error);
    } finally {
      setIsLoadingHistory(false);
    }
  }, [user?.id, token]);

  /**
   * 处理加载历史对话
   */
  const handleLoadConversations = useCallback(
    (loadedConversations: HistoryConversation[]) => {
      // 这个函数现在由 loadUserHistory 处理，但保留以保持接口一致
      console.log("Conversations loaded in sidebar:", loadedConversations);
    },
    []
  );

  /**
   * 根据第一条消息内容生成对话标题
   * @param firstMessage 第一条消息内容
   * @returns 生成的标题（截断至30字符）
   */
  const generateConversationTitle = (firstMessage: string): string => {
    const title =
      firstMessage.length > 30
        ? firstMessage.substring(0, 30) + "..."
        : firstMessage;
    return title || "新对话";
  };

  /**
   * 处理新建聊天：创建新对话并设置为活动对话
   */
  const handleNewChat = useCallback(() => {
    const newConversation: Conversation = {
      id: generateUniqueId("conv"), // 使用带前缀的唯一 ID
      title: "新对话",
      messages: [],
      createdAt: new Date(),
      updatedAt: new Date(),
    };

    setConversations((prev) => {
      // 确保新对话的ID不重复
      if (prev.some((conv) => conv.id === newConversation.id)) {
        newConversation.id = generateUniqueId("conv");
      }
      return [newConversation, ...prev];
    });
    setActiveConversationId(newConversation.id);
  }, []);

  /**
   * 处理选择对话：设置活动对话ID
   * @param conversationId 要选择的对话ID
   */
  const handleSelectConversation = useCallback((conversationId: string) => {
    setActiveConversationId(conversationId);
  }, []);

  /**
   * 处理删除对话：从列表中移除指定对话
   * @param conversationId 要删除的对话ID
   */
  const handleDeleteConversation = useCallback(
    (conversationId: string) => {
      setConversations((prev) =>
        prev.filter((conv) => conv.id !== conversationId)
      );
      if (activeConversationId === conversationId) {
        setActiveConversationId(undefined);
      }
    },
    [activeConversationId]
  );

  /**
   * 处理重命名对话：更新指定对话的标题
   * @param conversationId 要重命名的对话ID
   * @param newTitle 新标题
   */
  const handleRenameConversation = useCallback(
    (conversationId: string, newTitle: string) => {
      setConversations((prev) =>
        prev.map((conv) =>
          conv.id === conversationId
            ? { ...conv, title: newTitle, updatedAt: new Date() }
            : conv
        )
      );
    },
    []
  );

  /**
   * 处理新增消息：向当前活动对话添加新消息
   * @param message 新增的消息对象（包含角色和内容）
   */
  const handleMessageAdded = useCallback(
    (message: { role: "user" | "assistant"; content: string }) => {
      if (!activeConversationId) return;

      const newMessage = {
        id: generateUniqueId("msg"), // 使用带前缀的唯一 ID
        role: message.role,
        content: message.content,
        timestamp: new Date(),
      };

      setConversations((prev) =>
        prev.map((conv) =>
          conv.id === activeConversationId
            ? {
                ...conv,
                messages: [...conv.messages, newMessage],
                updatedAt: new Date(),
              }
            : conv
        )
      );
    },
    [activeConversationId]
  );

  /**
   * 处理第一条消息：根据第一条消息内容生成对话标题
   * @param content 第一条消息内容
   */
  const handleFirstMessage = useCallback(
    (content: string) => {
      if (!activeConversationId) return;

      const title = generateConversationTitle(content);
      setConversations((prev) =>
        prev.map((conv) =>
          conv.id === activeConversationId
            ? {
                ...conv,
                title,
                updatedAt: new Date(),
              }
            : conv
        )
      );
    },
    [activeConversationId]
  );

  // 在组件挂载时加载历史对话
  useEffect(() => {
    loadUserHistory();
  }, [loadUserHistory]);

  // 根据活动对话ID查找当前活动对话
  const activeConversation = conversations.find(
    (conv) => conv.id === activeConversationId
  );

  return (
    <div className="flex h-screen bg-background overflow-hidden">
      {/* 侧边栏组件 */}
      <Sidebar
        conversations={conversations}
        activeConversationId={activeConversationId}
        onNewChat={handleNewChat}
        onSelectConversation={handleSelectConversation}
        onDeleteConversation={handleDeleteConversation}
        onRenameConversation={handleRenameConversation}
        onLoadConversations={handleLoadConversations}
        isLoadingHistory={isLoadingHistory}
      />

      {/* 主聊天区域 */}
      <div className="flex-1 flex flex-col min-h-0">
        {" "}
        {/* 添加 min-h-0 防止 flex 溢出 */}
        <ChatWindow
          conversationId={activeConversationId}
          conversationTitle={activeConversation?.title}
          initialMessages={activeConversation?.messages || []}
          onMessageAdded={handleMessageAdded}
          onFirstMessage={handleFirstMessage}
        />
      </div>
    </div>
  );
}

// 受保护的主页组件（需要认证才能访问）
export default function ProtectedHomePage() {
  return (
    <ProtectedRoute>
      <HomePage />
    </ProtectedRoute>
  );
}
