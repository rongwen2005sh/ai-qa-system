FROM node:18-alpine

WORKDIR /app

# 安装 pnpm
RUN npm install -g pnpm

# 首先列出当前目录内容
RUN ls -la

# 复制包管理文件
COPY package.json pnpm-lock.yaml* ./
COPY tsconfig.json ./
COPY next.config.ts ./

# 再次列出内容，查看复制了哪些文件
RUN ls -la

# 尝试逐个复制目录
COPY ./lib ./lib/
RUN ls -la ./lib/ || echo "lib directory not found"

COPY ./components ./components/
COPY ./contexts ./contexts/
COPY ./public ./public/
COPY ./app ./app/
COPY ./types ./types/

# 安装依赖
RUN pnpm install --frozen-lockfile

# 构建应用
RUN pnpm build

EXPOSE 3000

CMD ["pnpm", "start"]