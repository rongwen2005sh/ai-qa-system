FROM node:18-alpine

# 安装 pnpm
RUN npm install -g pnpm

WORKDIR /app

# 复制包管理文件
COPY package.json pnpm-lock.yaml* ./
COPY tsconfig.json ./
COPY next.config.ts ./

# 复制源代码
COPY ./lib ./lib
COPY ./components ./components
COPY ./contexts ./contexts
COPY ./public ./public
COPY ./app ./app
COPY ./types ./types

# 使用 pnpm 安装依赖
RUN pnpm install --frozen-lockfile

# 构建应用
RUN pnpm build

EXPOSE 3000

CMD ["pnpm", "start"]