
import api from "@/api/api";

export const SettingsAPI = {
  getReport: () => api.get("/settings/admin/report"),
  updateReport: (data) => api.put("/settings/admin/report", data),

  getUniversity: () => api.get("/settings/admin/university"),
  updateUniversity: (data) => api.put("/settings/admin/university", data),
  loadLogo: () => api.get("/settings/admin/university/logo", { responseType: "blob" }),
  uploadLogo: (file) => {
    const formData = new FormData();
    formData.append("file", file);
    return api.post("/settings/admin/university/logo", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
  },
  getUbication: () => api.get("/settings/admin/ubication"),
  updateUbication: (data) => api.put("/settings/admin/ubication", data),
};
