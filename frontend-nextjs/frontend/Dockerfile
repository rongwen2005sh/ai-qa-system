FROM node:18-alpine

WORKDIR /app

# 安装 pnpm
RUN npm install -g pnpm

# 复制所有文件（确保 .dockerignore 不会排除重要文件）
COPY . .

# 安装依赖
RUN pnpm install --frozen-lockfile

# 尝试构建
RUN pnpm build

EXPOSE 3000
CMD ["pnpm", "start"]