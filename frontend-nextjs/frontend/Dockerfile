# 使用包含pnpm的官方镜像
FROM node:18-alpine

# 安装pnpm
RUN corepack enable && corepack prepare pnpm@latest --activate

RUN apk add --no-cache libc6-compat
WORKDIR /app

COPY package.json pnpm-lock.yaml* ./

# 使用corepack的pnpm
RUN pnpm install --frozen-lockfile

COPY . .

ENV NODE_ENV=production
ENV NEXT_TELEMETRY_DISABLED=1

RUN pnpm run build

RUN addgroup -g 1001 -S nodejs
RUN adduser -S nextjs -u 1001

USER nextjs

EXPOSE 3000
ENV PORT=3000

CMD ["pnpm", "start"]