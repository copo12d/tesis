import api from "@/api/api";

export const NotificationsAPI = {
  // Llama a la API cuando se invoque
  inform: (params) => api.get("/container/admin/alerts/full", { params }),
};
