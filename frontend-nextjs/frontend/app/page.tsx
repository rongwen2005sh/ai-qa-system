"use client"

import { useState, useCallback } from "react"
import { Sidebar } from "@/components/sidebar"
import { ChatWindow } from "@/components/chat-window"
import { ProtectedRoute } from "@/components/auth/protected-route"
import type { Conversation } from "@/types/chat"

// 主页组件，包含聊天界面和侧边栏
function HomePage() {
  const [conversations, setConversations] = useState<Conversation[]>([])
  const [activeConversationId, setActiveConversationId] = useState<string | undefined>()

  /**
   * 根据第一条消息内容生成对话标题
   * @param firstMessage 第一条消息内容
   * @returns 生成的标题（截断至30字符）
   */
  const generateConversationTitle = (firstMessage: string): string => {
    const title = firstMessage.length > 30 ? firstMessage.substring(0, 30) + "..." : firstMessage
    return title || "新对话"
  }

 /**
   * 处理新建聊天：创建新对话并设置为活动对话
   */
  const handleNewChat = useCallback(() => {
    const newConversation: Conversation = {
      id: crypto.randomUUID(),
      title: "新对话",
      messages: [],
      createdAt: new Date(),
      updatedAt: new Date(),
    }

    setConversations((prev) => [newConversation, ...prev])
    setActiveConversationId(newConversation.id)
  }, [])

  /**
   * 处理选择对话：设置活动对话ID
   * @param conversationId 要选择的对话ID
   */
  const handleSelectConversation = useCallback((conversationId: string) => {
    setActiveConversationId(conversationId)
  }, [])

  /**
   * 处理删除对话：从列表中移除指定对话
   * @param conversationId 要删除的对话ID
   */
  const handleDeleteConversation = useCallback(
    (conversationId: string) => {
      setConversations((prev) => prev.filter((conv) => conv.id !== conversationId))
      if (activeConversationId === conversationId) {
        setActiveConversationId(undefined)
      }
    },
    [activeConversationId],
  )

  /**
   * 处理重命名对话：更新指定对话的标题
   * @param conversationId 要重命名的对话ID
   * @param newTitle 新标题
   */
  const handleRenameConversation = useCallback((conversationId: string, newTitle: string) => {
    setConversations((prev) =>
      prev.map((conv) => (conv.id === conversationId ? { ...conv, title: newTitle, updatedAt: new Date() } : conv)),
    )
  }, [])

  /**
   * 处理新增消息：向当前活动对话添加新消息
   * @param message 新增的消息对象（包含角色和内容）
   */
  const handleMessageAdded = useCallback(
    (message: { role: "user" | "assistant"; content: string }) => {
      if (!activeConversationId) return

      const newMessage = {
        id: crypto.randomUUID(),
        role: message.role,
        content: message.content,
        timestamp: new Date(),
      }

      setConversations((prev) =>
        prev.map((conv) =>
          conv.id === activeConversationId
            ? {
                ...conv,
                messages: [...conv.messages, newMessage],
                updatedAt: new Date(),
              }
            : conv,
        ),
      )
    },
    [activeConversationId],
  )
  /**
   * 处理第一条消息：根据第一条消息内容生成对话标题
   * @param content 第一条消息内容
   */
  const handleFirstMessage = useCallback(
    (content: string) => {
      if (!activeConversationId) return

      const title = generateConversationTitle(content)
      setConversations((prev) =>
        prev.map((conv) =>
          conv.id === activeConversationId
            ? {
                ...conv,
                title,
                updatedAt: new Date(),
              }
            : conv,
        ),
      )
    },
    [activeConversationId],
  )

  // 根据活动对话ID查找当前活动对话
  const activeConversation = conversations.find((conv) => conv.id === activeConversationId)

  return (
    <div className="flex h-screen bg-background">
      {/* 侧边栏组件 */}
      <Sidebar
        conversations={conversations}
        activeConversationId={activeConversationId}
        onNewChat={handleNewChat}
        onSelectConversation={handleSelectConversation}
        onDeleteConversation={handleDeleteConversation}
        onRenameConversation={handleRenameConversation}
      />

        {/* 主聊天区域 */}
      <div className="flex-1 flex flex-col">
        <ChatWindow
          conversationId={activeConversationId}
          conversationTitle={activeConversation?.title}
          initialMessages={activeConversation?.messages || []}
          onMessageAdded={handleMessageAdded}
          onFirstMessage={handleFirstMessage}
        />
      </div>
    </div>
  )
}

// 受保护的主页组件（需要认证才能访问）
export default function ProtectedHomePage() {
  return (
    <ProtectedRoute>
      <HomePage />
    </ProtectedRoute>
  )
}
