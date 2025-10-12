import api from "@/api/api"; // Ajusta la ruta si tu base api está en otro lado

export const BatchAPI = {
  // Obtener todos los lotes (paginado, ordenado)
  getAll: (params = {}) =>
    api.get("/batch/admin/all", {
      params: {
        page: params.page ?? 0,
        size: params.size ?? 10,
        sortBy: params.sortBy ?? "creationDate",
        sortDir: params.sortDir ?? "desc",
        ...params,
      },
    }),

  // Registrar un nuevo lote
  register: (data) =>
    api.post("/batch/admin/register", data),

  // Procesar un lote por ID
  process: (id) =>
    api.post(`/batch/admin/process/${id}`),

  // Obtener un lote específico por ID
  getOne: (id) =>
    api.get(`/batch/admin/${id}`),

  // Soft delete de un lote por ID
  softDelete: (id) =>
    api.delete(`/batch/admin/delete/${id}`),
};