<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { api } from '../api'
import { useAuth } from '../stores/auth'

interface Comment {
  id: number
  author: string
  content: string
  createdAt: string
}

interface PostDetail {
  id: number
  title: string
  content: string
  author: string
  createdAt: string
  comments: Comment[]
}

const route = useRoute()
const router = useRouter()
const { isLoggedIn } = useAuth()

const post = ref<PostDetail | null>(null)
const loading = ref(true)
const notFound = ref(false)

const comment = ref('')
const error = ref('')
const submitting = ref(false)

async function load() {
  loading.value = true
  notFound.value = false
  try {
    post.value = await api<PostDetail>(`/forum/posts/${route.params.id}`)
  } catch {
    notFound.value = true
  } finally {
    loading.value = false
  }
}

async function submitComment() {
  error.value = ''
  if (!isLoggedIn.value) {
    router.push({ path: '/auth', query: { redirect: route.fullPath } })
    return
  }
  if (!comment.value.trim()) {
    error.value = '评论不能为空'
    return
  }
  submitting.value = true
  try {
    await api(`/forum/posts/${route.params.id}/comments`, {
      method: 'POST',
      body: JSON.stringify({ content: comment.value.trim() }),
    })
    comment.value = ''
    await load()
  } catch (err) {
    error.value = err instanceof Error ? err.message : '评论失败，请稍后再试'
  } finally {
    submitting.value = false
  }
}

function formatTime(iso: string) {
  if (!iso) return ''
  return iso.replace('T', ' ').slice(0, 16)
}

onMounted(load)
</script>

<template>
  <div class="thread container">
    <nav class="breadcrumb reveal" aria-label="面包屑">
      <RouterLink to="/forum">论坛</RouterLink>
      <span class="sep">/</span>
      <span class="current">{{ post?.title ?? '帖子' }}</span>
    </nav>

    <p v-if="loading" class="state-text">正在读取帖子…</p>

    <section v-else-if="notFound" class="state">
      <p class="state__char">逝</p>
      <p class="state__text">这个帖子不存在，或者已经被删掉了。</p>
      <RouterLink to="/forum" class="btn">回到论坛</RouterLink>
    </section>

    <template v-else-if="post">
      <article class="post reveal">
        <h1 class="post-title">{{ post.title }}</h1>
        <p class="post-meta">{{ post.author }} · {{ formatTime(post.createdAt) }}</p>
        <div class="post-content">{{ post.content }}</div>
      </article>

      <section class="comments">
        <h2 class="comments-title">{{ post.comments.length }} 条回复</h2>

        <div v-if="post.comments.length" class="comment-list">
          <div v-for="item in post.comments" :key="item.id" class="comment">
            <div class="comment-head">
              <span class="comment-author">{{ item.author }}</span>
              <span class="comment-time">{{ formatTime(item.createdAt) }}</span>
            </div>
            <p class="comment-content">{{ item.content }}</p>
          </div>
        </div>
        <p v-else class="state-text">还没有回复，来抢沙发。</p>

        <form class="comment-form" @submit.prevent="submitComment">
          <textarea
            v-model="comment"
            rows="3"
            maxlength="1000"
            :placeholder="isLoggedIn ? '写下你的回复…' : '登录后才能回复，点这里去登录'"
          ></textarea>
          <p v-if="error" class="form-error" role="alert">{{ error }}</p>
          <button type="submit" class="btn btn--solid" :disabled="submitting">
            {{ submitting ? '发送中…' : isLoggedIn ? '回 复' : '去登录' }}
          </button>
        </form>
      </section>
    </template>
  </div>
</template>

<style scoped>
.thread {
  max-width: 860px;
  padding-top: 64px;
  padding-bottom: 96px;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12px;
  letter-spacing: 0.12em;
  color: var(--dim);
}

.breadcrumb a {
  color: var(--muted);
  transition: color 0.3s;
}

.breadcrumb a:hover {
  color: var(--soul);
}

.breadcrumb .sep {
  opacity: 0.5;
}

.breadcrumb .current {
  color: var(--text);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.post {
  margin-top: 36px;
  padding-bottom: 36px;
  border-bottom: 1px solid var(--line);
}

.post-title {
  font-size: clamp(26px, 3vw, 36px);
  letter-spacing: 0.08em;
  color: var(--bone);
}

.post-meta {
  margin-top: 14px;
  font-size: 12px;
  letter-spacing: 0.1em;
  color: var(--dim);
}

.post-content {
  margin-top: 26px;
  font-size: 15px;
  line-height: 1.95;
  color: #bcc8c3;
  white-space: pre-wrap;
  word-break: break-word;
}

.comments {
  margin-top: 44px;
}

.comments-title {
  font-size: 18px;
  letter-spacing: 0.1em;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--line);
}

.comment-list {
  display: flex;
  flex-direction: column;
}

.comment {
  padding: 18px 2px;
  border-bottom: 1px solid var(--line);
}

.comment-head {
  display: flex;
  align-items: baseline;
  gap: 14px;
}

.comment-author {
  font-size: 13px;
  color: var(--soul);
}

.comment-time {
  font-size: 11px;
  color: var(--dim);
}

.comment-content {
  margin-top: 8px;
  font-size: 14px;
  line-height: 1.85;
  color: var(--text);
  white-space: pre-wrap;
  word-break: break-word;
}

.comment-form {
  margin-top: 28px;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.comment-form textarea {
  border: 1px solid var(--line);
  background: rgba(233, 240, 236, 0.02);
  padding: 12px 14px;
  color: var(--bone);
  font-family: var(--font-body);
  font-size: 14px;
  outline: none;
  resize: vertical;
  transition: border-color 0.3s;
}

.comment-form textarea:focus {
  border-color: rgba(169, 230, 218, 0.6);
}

.comment-form .btn {
  align-self: flex-start;
}

.form-error {
  font-size: 12.5px;
  color: #d98f8f;
}

.state {
  padding-block: 72px;
  text-align: center;
}

.state__char {
  font-family: var(--font-display);
  font-size: 96px;
  line-height: 1;
  color: rgba(233, 240, 236, 0.08);
}

.state__text,
.state-text {
  margin-top: 16px;
  font-size: 13px;
  letter-spacing: 0.1em;
  color: var(--muted);
}

.state .btn {
  margin-top: 28px;
}
</style>
