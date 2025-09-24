FROM node:18-alpine

WORKDIR /app

# 安装 pnpm
RUN npm install -g pnpm

# 复制包管理文件
COPY package.json pnpm-lock.yaml* ./

# 安装依赖
RUN pnpm install --frozen-lockfile

# 复制源代码（.dockerignore 会过滤不必要的文件）
COPY . .

# 构建应用
RUN pnpm build

EXPOSE 3000
CMD ["pnpm", "start"]