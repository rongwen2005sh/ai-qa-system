/**
 * 对话接口定义
 * 表示一个完整的聊天对话会话
 */
export interface Conversation {
  id: string; // 对话的唯一标识符
  title: string; // 对话标题，通常由第一条消息生成
  messages: Array<{
    // 消息数组，包含对话中的所有消息
    id: string; // 消息的唯一标识符
    role: "user" | "assistant"; // 消息发送者角色：用户或AI助手
    content: string; // 消息内容文本
    timestamp: Date; // 消息发送时间
  }>;
  createdAt: Date; // 对话创建时间
  updatedAt: Date; // 对话最后更新时间（最后一条消息的时间）
}

/**
 * 聊天消息接口定义
 * 表示单条聊天消息的结构
 */
export interface ChatMessage {
  id: string; // 消息的唯一标识符
  role: "user" | "assistant"; // 消息发送者角色：用户或AI助手
  content: string; // 消息内容文本
  timestamp: Date; // 消息发送时间
}
