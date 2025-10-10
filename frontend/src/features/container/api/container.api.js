import api from "@/api/api";

export const ContainerAPI = {
  // Listado con paginación, orden y búsqueda (si aplica)
  list: (params = {}) =>
    api.get("/container/admin/all", { params }),

  // Crear contenedor
  create: (payload) => api.post("/container/admin/register", payload),

  // Obtener tipos de contenedor (all-list)
  getTypes: () => api.get("/container-type/admin/all-list"),

  // Obtener QR
  getQr: (id) => api.get(`/container/admin/qr/${id}`),

  // Eliminar contenedor
  delete: (id) => api.delete(`/container/admin/delete/${id}`),
};