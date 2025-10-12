import api from "../../../api/api";

export const ContainerTypeAPI = {
  register: (payload) => api.post("/container-type/admin/register", payload),
  list: ({ name = "", page = 0, size = 10, sortBy = "id", sortDir = "DESC" } = {}) =>
    api.get("/container-type/admin/all", {
      params: { name, page, size, sortBy, sortDir },
    }),
  getById: (id) => api.get(`/container-type/admin/${id}`),
  update: (id, payload) => api.put(`/container-type/admin/update/${id}`, payload),
  delete: (id) => api.delete(`/container-type/admin/delete/${id}`),
  // ...otros métodos si los necesitas
};