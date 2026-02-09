import apiClient from '@/plugins/api-client.js'

// --- 1. SINGLE CAR OPERATIONS (Create/Edit/Get/Delete) ---

export const createCar = async (
  payload,
  skipToast = false
) => {
  const { data } = await apiClient.post('/cars', payload, {
    skipToast: skipToast,
  })
  return data
}

export const fetchCarById = async (id) => {
  const { data } = await apiClient.get(`/cars/${id}`)
  return data
}

export const updateCar = async (
  id,
  payload,
  skipToast = false
) => {
  const { data } = await apiClient.patch(
    `/cars/${id}`,
    payload,
    { skipToast }
  )
  return data
}

export const deleteCar = async (id, skipToast = false) => {
  return apiClient.delete(`/cars/${id}`, { skipToast })
}



export const fetchCars = async (params) => {
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
    serialNumber: params.filters?.serialNumber || null,
    brand: params.filters?.brand || null,
    model: params.filters?.model || null,
    carType: params.filters?.carType || null,
    fuelType: params.filters?.fuelType || null,
    ownerId: params.ownerId || null,
    mechanicId: params.mechanicId || null,
    withOwnerInfo: params.withOwnerInfo || null,
    sort: sortParam || null,
  }

  const cleanParams = Object.fromEntries(
    Object.entries(requestParams).filter(
      ([_, v]) => v != null && v !== ''
    )
  )

  const response = await apiClient.get('/cars', {
    params: cleanParams,
  })

  const data = response.data
  const content = data._embedded?.cars || []
  const totalElements =
    data.page?.totalElements ?? data.totalElements ?? 0

  return {
    content,
    totalElements,
  }
}

export const deleteMultipleCars = async (
  ids,
  skipToast = false
) => {
  const response = await apiClient.delete(
    '/cars/bulk-delete',
    {
      data: ids,
      skipToast,
    }
  )
  return response.data
}


export const downloadCarImportTemplate = (type) => {
  return apiClient.get(`/cars/template/${type}`, {
    responseType: 'blob',
  })
}

export const importCarsFile = async (file) => {
  const formData = new FormData()
  formData.append('file', file)

  const isExcel =
    file.name.endsWith('.xlsx') ||
    file.name.endsWith('.xls')
  const endpoint = isExcel ? '/cars/excel' : '/cars/csv'

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
