"use client"

import { useState, useCallback } from "react"
import { Sidebar } from "@/components/sidebar"
import { ChatWindow } from "@/components/chat-window"
import { ProtectedRoute } from "@/components/auth/protected-route"
import type { Conversation } from "@/types/chat"

function HomePage() {
  const [conversations, setConversations] = useState<Conversation[]>([])
  const [activeConversationId, setActiveConversationId] = useState<string | undefined>()

  const generateConversationTitle = (firstMessage: string): string => {
    const title = firstMessage.length > 30 ? firstMessage.substring(0, 30) + "..." : firstMessage
    return title || "新对话"
  }

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

  const handleSelectConversation = useCallback((conversationId: string) => {
    setActiveConversationId(conversationId)
  }, [])

  const handleDeleteConversation = useCallback(
    (conversationId: string) => {
      setConversations((prev) => prev.filter((conv) => conv.id !== conversationId))
      if (activeConversationId === conversationId) {
        setActiveConversationId(undefined)
      }
    },
    [activeConversationId],
  )

  const handleRenameConversation = useCallback((conversationId: string, newTitle: string) => {
    setConversations((prev) =>
      prev.map((conv) => (conv.id === conversationId ? { ...conv, title: newTitle, updatedAt: new Date() } : conv)),
    )
  }, [])

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

  const activeConversation = conversations.find((conv) => conv.id === activeConversationId)

  return (
    <div className="flex h-screen bg-background">
      <Sidebar
        conversations={conversations}
        activeConversationId={activeConversationId}
        onNewChat={handleNewChat}
        onSelectConversation={handleSelectConversation}
        onDeleteConversation={handleDeleteConversation}
        onRenameConversation={handleRenameConversation}
      />

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

export default function ProtectedHomePage() {
  return (
    <ProtectedRoute>
      <HomePage />
    </ProtectedRoute>
  )
}
