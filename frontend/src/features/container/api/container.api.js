import api from "@/api/api";

export const ContainerAPI = {
  // Listado con paginación, orden y búsqueda (si aplica)
  list: (params = {}) =>
    api.get("/container/admin/all", { params }),

  // Obtener contenedor por ID
  getById: (id) => api.get(`/container/admin/${id}`),

  // Obtener contenedor por ID para editar
  getByIdEdit: (id) => api.get(`/container/admin/edit/${id}`),

  // Crear contenedor
  create: (payload) => api.post("/container/admin/register", payload),

  // Obtener tipos de contenedor (all-list)
  getTypes: () => api.get("/container-type/admin/all-list"),

  // Obtener QR
  getQr: (id) => api.get(`/container/admin/qr/${id}`),

  // Eliminar contenedor
  delete: (id) => api.delete(`/container/admin/delete/${id}`),

  // actualizar contenedor
  update: (id, payload) => api.put(`/container/admin/update/${id}`, payload),
};