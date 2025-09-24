FROM node:18-alpine

WORKDIR /app

# 安装 pnpm
RUN npm install -g pnpm

# 首先列出当前目录内容
RUN ls -la

# 首先复制配置文件
COPY package.json pnpm-lock.yaml* ./
COPY tsconfig.json ./
COPY next.config.ts ./
COPY next-env.d.ts ./

# 使用更安全的复制方式，避免目录不存在的问题
COPY . .

# 安装依赖
RUN pnpm install --frozen-lockfile

# 构建应用
RUN pnpm build

EXPOSE 3000

CMD ["pnpm", "start"]