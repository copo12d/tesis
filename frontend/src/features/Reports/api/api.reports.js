import api from "@/api/api";

export const ReportsAPI = {
  downloadBatch1: () =>
    api.get("/reports/admin/batch/1", { responseType: "blob" }),
  downloadBatch2: () =>
    api.get("/reports/admin/batch/2", { responseType: "blob" }),
  // Modifica para aceptar params
  downloadUsers: (params = {}) =>
    api.get("/reports/admin/users/all", {
      responseType: "blob",
      params: { sortBy: "user", sortDir: "ASC", ...params },
    }),
  downloadContainers: (params = {}) =>
    api.get("/reports/admin/containers/all", {
      responseType: "blob",
      params: { sortBy: "createAt", sortDir: "ASC", ...params },
    }),
};