declare namespace API {
  type BaseResponseBoolean_ = {
    code?: number
    data?: boolean
    message?: string
  }

  type BaseResponseLong_ = {
    code?: number
    data?: number
    message?: string
  }

  type BaseResponsePageUser_ = {
    code?: number
    data?: PageUser_
    message?: string
  }

  type BaseResponsePageUserVO_ = {
    code?: number
    data?: PageUserVO_
    message?: string
  }

  type BaseResponseUser_ = {
    code?: number
    data?: User
    message?: string
  }

  type BaseResponseUserVO_ = {
    code?: number
    data?: UserVO
    message?: string
  }

  type DeleteRequest = {
    id?: number
  }

  type getUserByIdUsingGETParams = {
    /** id */
    id?: number
  }

  type getUserVOByIdUsingGETParams = {
    /** id */
    id?: number
  }

  type PageUser_ = {
    current?: number
    pages?: number
    records?: User[]
    size?: number
    total?: number
  }

  type PageUserVO_ = {
    current?: number
    pages?: number
    records?: UserVO[]
    size?: number
    total?: number
  }

  type User = {
    attentionCount?: number
    avatar?: string
    createTime?: string
    editTime?: string
    fansCount?: number
    id?: number
    isDelete?: number
    isVip?: number
    password?: string
    phone?: string
    updateTime?: string
    userName?: string
    userType?: number
  }

  type UserAddRequest = {
    avatar?: string
    isVip?: number
    password?: string
    phone?: string
    userName?: string
    userType?: number
  }

  type UserEditRequest = {
    avatar?: string
    id?: number
    phone?: string
    userName?: string
  }

  type UserLoginRequest = {
    password?: string
    phone?: string
  }

  type UserQueryRequest = {
    attentionCount?: number
    current?: number
    fansCount?: number
    id?: number
    isVip?: number
    pageSize?: number
    phone?: string
    sortField?: string
    sortOrder?: string
    userName?: string
    userType?: number
  }

  type UserRegisterRequest = {
    checkPassword?: string
    password?: string
    phone?: string
  }

  type UserUpdateRequest = {
    avatar?: string
    id?: number
    isVip?: number
    phone?: string
    userName?: string
    userType?: number
  }

  type UserVO = {
    attentionCount?: number
    avatar?: string
    createTime?: string
    fansCount?: number
    id?: number
    isVip?: number
    phone?: string
    userName?: string
    userType?: number
  }
}
