import api from "@/api/api";

export const ReportsAPI = {
  // Reporte: todos los lotes (opcionalmente filtrado por fecha)
  downloadAllBatches: (params = {}) =>
    api.get("/reports/admin/batch/all", {
      responseType: "blob",
      params: {
        // el backend espera 'fechaInicio' y 'fechaFin'
        ...(params.fechaInicio ? { fechaInicio: params.fechaInicio } : {}),
        ...(params.fechaFin ? { fechaFin: params.fechaFin } : {}),
      },
    }),

  // Reporte: detalle de un lote
  downloadBatch: (id) =>
    api.get(`/reports/admin/batch/${id}`, { responseType: "blob" }),

  // Reportes existentes
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

  // Lista paginada de lotes para combos/tablas
  getAllBatches: (params = {}) =>
    api.get("/batch/admin/all", {
      params: {
        page: params.page ?? 0,
        size: params.size ?? 10,
        sortBy: params.sortBy ?? "creationDate",
        sortDir: params.sortDir ?? "desc",
        ...params,
      },
    }),

  getProcessBatchDropdown: () =>
    api.get("/batch/admin/dropdown/process"),

};
