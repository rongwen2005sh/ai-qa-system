import { NextResponse } from "next/server";

export async function GET(request: Request) {
  // 在这里可以进行数据库查询等后端操作
  const data = { message: "Hello from the Server-side API!" };

  // 返回 JSON 响应
  return NextResponse.json(data);
}
