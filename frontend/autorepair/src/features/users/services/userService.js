import apiClient from '@/plugins/api-client.js'



export const createUser = async (
  payload,
  skipToast = false
) => {
  const { data } = await apiClient.post('/users', payload, {
    skipToast: skipToast,
  })
  return data
}

export const fetchUserById = async (id) => {
  const { data } = await apiClient.get(`/users/${id}`)
  return data
}


export const updateUser = async (
  id,
  payload,
  skipToast = false
) => {
  const { data } = await apiClient.patch(
    `/users/${id}`,
    payload,
    { skipToast }
  )
  return data
}


export const deleteUser = async (id, skipToast = false) => {
  return apiClient.delete(`/users/${id}`, { skipToast })
}


export const setUserActiveStatus = (userId, isActive) => {
  return apiClient.patch(
    `/users/${userId}/active-account`,
    null,
    {
      params: { active: isActive },
      skipToast: true,
    }
  )
}


export const fetchUsers = async (params) => {
  // 1. Prepare Sort Parameter
  let sortParam = null
  if (params.sortField) {
    const direction =
      params.sortOrder === 1 ? 'asc' : 'desc'
    sortParam = `${params.sortField},${direction}`
  }


  const requestParams = {
    page: params.page,
    size: params.size,
    keyword: params.filters?.global || null,
    username: params.filters?.username || null,
    lastName: params.filters?.lastName || null,
    taxNumber: params.filters?.taxNumber || null,
    role: params.role || null,
    sort: sortParam || null,
  }

  const cleanParams = Object.fromEntries(
    Object.entries(requestParams).filter(
      ([_, v]) => v != null && v !== ''
    )
  )

  const response = await apiClient.get('/users', {
    params: cleanParams,
  })
  const data = response.data

  const content = data._embedded?.users || []
  const totalElements =
    data.page?.totalElements ?? data.totalElements ?? 0

  return {
    content,
    totalElements,
  }
}

export const deleteMultipleUsers = async (
  ids,
  skipToast = false
) => {
  const response = await apiClient.delete(
    '/users/bulk-delete',
    {
      data: ids,
      skipToast,
    }
  )
  return response.data
}


export const fetchPendingUsers = async (
  page,
  size,
  sortBy,
  sortOrder
) => {
  const response = await apiClient.get(
    '/users/request-activation',
    {
      params: { page, size, sortBy, sortOrder },
    }
  )

  const data = response.data
  const content = data._embedded?.users || []
  const totalElements =
    data.page?.totalElements ?? data.totalElements ?? 0

  return {
    content,
    totalElements,
  }
}

export const denyUserAccount = (userId) => {
  return apiClient.delete(`/users/${userId}`)
}


export const downloadImportTemplate = (type) => {
  return apiClient.get(`/users/template/${type}`, {
    responseType: 'blob',
  })
}

export const importUsersFile = async (file) => {
  const formData = new FormData()
  formData.append('file', file)

  const isExcel =
    file.name.endsWith('.xlsx') ||
    file.name.endsWith('.xls')
  const endpoint = isExcel ? '/users/excel' : '/users/csv'

  try {
    const response = await apiClient.post(
      endpoint,
      formData,
      {
        headers: { 'Content-Type': 'multipart/form-data' },
        skipErrorToast: true,
      }
    )
    return response.data
  } catch (error) {

    let errorMessage = 'Upload failed'

    if (error.response && error.response.data) {
      errorMessage =
        typeof error.response.data === 'string'
          ? error.response.data
          : error.response.data.message ||
            JSON.stringify(error.response.data)
    }

    throw new Error(errorMessage)
  }
}


export const exportCustomerProfile = async (
  userId,
  format
) => {
  const extension = format === 'excel' ? 'xlsx' : 'pdf'

  try {
    const response = await apiClient.get(
      `/users/${userId}/export-profile/${format}`,
      {
        responseType: 'blob',
      }
    )

    const url = window.URL.createObjectURL(
      new Blob([response.data])
    )
    const link = document.createElement('a')

    const filename = `customer_${userId}_profile.${extension}`

    link.href = url
    link.setAttribute('download', filename)

    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
  } catch (error) {
    console.error(
      `Failed to export profile as ${format}`,
      error
    )
    throw error
  }
}
