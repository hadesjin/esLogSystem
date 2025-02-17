<template>
  <div class="app-container">
    <el-container>
      <el-header>
        <h1>日志查询系统</h1>
      </el-header>
      <el-main>
        <el-form :model="queryForm" :rules="rules" ref="formRef" class="query-form">
          <el-row :gutter="20">
            <el-col :span="6">
              <el-form-item label="服务名称" prop="serviceName" required>
                <el-select
                  v-model="queryForm.serviceName"
                  filterable
                  placeholder="请选择服务名称"
                  :loading="loading"
                >
                  <el-option
                    v-for="index in indices"
                    :key="index"
                    :label="index"
                    :value="index"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="TID">
                <el-input v-model="queryForm.tid" placeholder="请输入TID"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="时间范围">
                <el-date-picker
                  v-model="timeRange"
                  type="datetimerange"
                  range-separator="至"
                  start-placeholder="开始时间"
                  end-placeholder="结束时间"
                ></el-date-picker>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="6">
              <el-form-item label="AND关键词">
                <el-input
                  v-model="queryForm.messageAndListText"
                  placeholder="请输入关键词，多个关键词以逗号分隔"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="OR关键词">
                <el-input
                  v-model="queryForm.messageOrListText"
                  placeholder="请输入关键词，多个关键词以逗号分隔"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="日志级别">
                <el-input v-model="queryForm.logLevel" placeholder="请输入日志级别"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="排序方式">
                <el-select v-model="queryForm.sortOrder" placeholder="请选择">
                  <el-option label="升序" value="asc"></el-option>
                  <el-option label="降序" value="desc"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24" class="query-buttons">
              <div class="env-switch-container">
                <div class="env-switch-wrapper" @click="toggleEnv">
                  <div class="env-switch-label">bx</div>
                  <div class="env-switch-label">wgq</div>
                  <div class="env-switch-indicator" :class="{ 'is-wgq': queryForm.env === 'wgq' }"></div>
                </div>
              </div>
              <el-button
                :type="isQuerying ? 'danger' : 'success'"
                @click="toggleQuery"
              >{{ isQuerying ? '停止' : '开始' }}</el-button>
              <el-button type="warning" @click="clearLogs">清除</el-button>
            </el-col>
          </el-row>
        </el-form>

        <div class="log-container">
          <div v-for="(log, index) in logs" :key="index" class="log-item">
            <span v-html="highlightKeywords(log.logMessage)"></span>
          </div>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'

// 创建axios取消令牌
let currentRequest = null

// 存储ES索引列表
const indices = ref([])
const loading = ref(false)

// 获取所有ES索引
const fetchIndices = async () => {
  try {
    loading.value = true
    const response = await axios.get(`http://localhost:8080/api/logs/indices?env=${queryForm.value.env}`)
    indices.value = response.data
  } catch (error) {
    console.error('获取索引列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 组件挂载时获取索引列表
onMounted(() => {
  fetchIndices()
})

// 查询间隔时间配置（单位：毫秒）
const QUERY_INTERVAL = 30000

const queryForm = ref({
  serviceName: '',
  logLevel: '',
  tid: '',
  messageAndListText: '',
  messageOrListText: '',
  messageAndList: [],
  messageOrList: [],
  sortField: '@timestamp',
  sortOrder: 'asc',
  pageNum: 1,
  pageSize: 500,
  env: 'bx'
})

const isQuerying = ref(false)
const timeRange = ref([ new Date(new Date() - 600000), // 当前时间
  new Date()])
const logs = ref([])
let queryInterval = null

const rules = {
  serviceName: [{ required: true, message: '请输入服务名称', trigger: 'blur' }]
}

const formRef = ref(null)

const toggleQuery = () => {
  if (isQuerying.value) {
    stopQuery()
  } else {
    formRef.value.validate((valid) => {
      if (valid) {
        startQuery()
      }
    })
  }
}

const startQuery = () => {
  isQuerying.value = true
  queryLogs()
  
  //开始查询先查询间隔时间的日志
  // if (!timeRange.value || timeRange.value.length !== 2) {
  //     const endTime = new Date()
  //     const startTime = new Date(endTime.getTime() - QUERY_INTERVAL) // 1秒前
  //     console.log("初次开始时间："+startTime.toString())
  //     console.log("初次结束时间："+endTime.toString())
  //     // 更新查询参数
  //     queryForm.value = {
  //       ...queryForm.value,
  //       startTime: startTime.toISOString(),
  //       endTime: endTime.toISOString()
  //     }
  //     queryLogs()
  // }

  //休眠间隔时间
  // setTimeout(() => {console.log('休眠完成');}, QUERY_INTERVAL);

  //每5s查询一次。
  // queryInterval = setInterval(queryLogs, QUERY_INTERVAL)

  // 只有在没有设置时间范围时才启动定时查询。每秒查询一次
  if (!timeRange.value || timeRange.value.length !== 2) {
    // 新的3秒查询逻辑
    queryInterval = setInterval(() => {
      // 计算上三秒的时间范围
      const endTime = new Date()
      const startTime = new Date(endTime.getTime() - QUERY_INTERVAL) // 1秒前
      console.log("重复开始时间："+startTime.toString())
      console.log("重复结束时间："+endTime.toString())
      // 更新查询参数
      queryForm.value = {
        ...queryForm.value,
        startTime: startTime.toISOString(),
        endTime: endTime.toISOString()
      }
      queryLogs()
    }, QUERY_INTERVAL) // 每3秒查询一次
  }
}

const stopQuery = () => {
  isQuerying.value = false
  if (queryInterval) {
    clearInterval(queryInterval)
    queryInterval = null
  }
  // 重置查询相关状态
  if (!timeRange.value || timeRange.value.length !== 2) {
    timeRange.value = []
  }
  // 停止所有进行中的请求
  if (currentRequest) {
    currentRequest.cancel()
    currentRequest = null
  }
}

const queryLogs = async () => {
  try {
    // 如果有正在进行的请求，取消它
    if (currentRequest) {
      currentRequest.cancel()
    }
    // 创建新的取消令牌
    currentRequest = axios.CancelToken.source()
    const requestData = {
      ...queryForm.value,
      messageAndList: queryForm.value.messageAndListText.split(',').filter(item => item.trim()),
      messageOrList: queryForm.value.messageOrListText.split(',').filter(item => item.trim())
    }

    // 如果设置了时间范围，使用设置的时间
    if (timeRange.value && timeRange.value.length === 2) {
      requestData.startTime = timeRange.value[0].toISOString()
      requestData.endTime = timeRange.value[1].toISOString()
      
      // 如果设置了时间范围，查询完就停止
      const response = await axios.post('http://localhost:8080/api/logs/query', requestData)
      const newLogs = response.data.logs || []
      logs.value = newLogs
      stopQuery()
    } else {
      // 如果没有设置时间范围，使用实时查询的结果
      const response = await axios.post('http://localhost:8080/api/logs/query', requestData)
      const newLogs = response.data.logs || []
      logs.value = [...logs.value, ...newLogs]
      
      // 保持最多1000条记录
      if (logs.value.length > 1000) {
        logs.value = logs.value.slice(logs.value.length - 1000)
      }
    }
  } catch (error) {
    console.error('查询日志失败:', error)
    stopQuery()
  }
}

const clearLogs = () => {
  logs.value = []
}

const highlightKeywords = (text) => {
  if (!text) return ''
  
  const keywords = [
    ...queryForm.value.messageAndListText.split(',').filter(item => item.trim()),
    ...queryForm.value.messageOrListText.split(',').filter(item => item.trim()),
    queryForm.value.tid ? queryForm.value.tid.trim() : ''
  ].filter(Boolean)
  
  let highlightedText = text
  keywords.forEach(keyword => {
    if (keyword) {
      const regex = new RegExp(keyword, 'gi')
      highlightedText = highlightedText.replace(regex, match => 
        `<span class="highlight">${match}</span>`
      )
    }
  })
  
  return highlightedText
}
const toggleEnv = () => {
  queryForm.value.env = queryForm.value.env === 'bx' ? 'wgq' : 'bx'
  fetchIndices()
}
</script>

<style>
.app-container {
  padding: 20px;
}

.query-form {
  margin-bottom: 20px;
}

.query-buttons {
  text-align: right;
  margin-top: 20px;
}

.log-container {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 10px;
  height: 600px;
  overflow-y: auto;
}

.log-item {
  padding: 5px 0;
  border-bottom: 1px solid #ebeef5;
  line-height: 1.5;
}

.highlight {
  color: red;
  font-weight: bold;
}
.env-switch-container {
  display: inline-block;
  margin-right: 10px;
}

.env-switch-wrapper {
  position: relative;
  width: 100px;
  height: 32px;
  background-color: #f5f7fa;
  border-radius: 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 12px;
  box-sizing: border-box;
  border: 1px solid #dcdfe6;
}

.env-switch-label {
  color: #606266;
  font-size: 14px;
  z-index: 1;
}

.env-switch-indicator {
  position: absolute;
  left: 2px;
  width: 45px;
  height: 28px;
  background-color: #409eff;
  border-radius: 14px;
  transition: transform 0.3s ease;
}

.env-switch-indicator.is-wgq {
  transform: translateX(51px);
}
</style>