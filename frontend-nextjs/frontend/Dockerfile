FROM node:18-alpine

WORKDIR /app

# 安装 pnpm 和 curl（用于健康检查）- 保持这个配置
RUN npm install -g pnpm && \
    apk add --no-cache curl

# 复制包管理文件
COPY package.json pnpm-lock.yaml* ./

# 安装依赖
RUN pnpm install --frozen-lockfile

# 复制源代码
COPY . .

# 构建应用
RUN pnpm build

EXPOSE 3000
CMD ["pnpm", "start"]