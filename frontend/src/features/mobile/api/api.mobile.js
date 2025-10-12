import api from "@/api/api";

export const MobileAPI = {
  // Obtener información de un contenedor por ID (público)
  getContainer: (id) => api.get(`/container/public/${id}`),

  // Reportar un contenedor por serial
  reportContainer: (serial) =>
    api.post(`/container/public/report`, null, { params: { serial } }),

  // Registrar waste
  registerWaste: ({ weight, containerId, batchId }) =>
    api.post("/waste/admin/register", { weight, containerId, batchId }),

  // Obtener batches en progreso (dropdown)
  getInProgressBatches: () => api.get("/batch/admin/dropdown/in-progress"),
};