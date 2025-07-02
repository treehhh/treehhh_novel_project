<template>
  <div id="userModal">
    <a-modal v-model:open="open" title="Basic Modal" :footer="false" @cancel="closeModal">
      <a-form :model="formState" layout="vertical" @finish="onFinish">
        <a-form-item label="用户名" name="userName">
          <a-input v-model:value="formState.userName" />
        </a-form-item>

        <a-form-item label="用户类型">
          <a-radio-group v-model:value="formState.userType">
            <a-radio value="0">用户</a-radio>
            <a-radio value="1">作者</a-radio>
          </a-radio-group>
        </a-form-item>

        <a-form-item label="是否会员">
          <a-radio-group v-model:value="formState.isVip">
            <a-radio value="0">否</a-radio>
            <a-radio value="1">是</a-radio>
          </a-radio-group>
        </a-form-item>

        <a-form-item :wrapper-col="{ offset: 8, span: 16 }">
          <a-button type="primary" html-type="submit">确认</a-button>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>
<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { addUserUsingPost } from '@/api/userController.ts'
import { message } from 'ant-design-vue'

const open = ref<boolean>(false)

// 定义组件属性类型
interface Props {
  onSuccess: () => void
}

// 给组件指定初始值
const props = withDefaults(defineProps<Props>(), {})

const formState = reactive<API.UserAddRequest>({
  userName: '',
  userType: 0,
  isVip: 0,
})

const showModal = () => {
  open.value = true
}

const closeModal = () => {
  open.value = false
}

defineExpose({
  showModal,
})

const onFinish = async () => {
  const res = await addUserUsingPost(formState)
  if (res.data.code == 0 && res.data.data) {
    message.success('添加成功')
    props.onSuccess?.()
    closeModal()
  } else {
    message.error('添加失败')
  }
}
</script>
<style scoped></style>
