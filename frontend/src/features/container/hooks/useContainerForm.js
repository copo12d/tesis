import { useState } from "react";

export function useContainerForm({ initialValues = {}, onSubmit }) {
  const [form, setForm] = useState({
    serial: "",
    latitude: "",
    longitude: "",
    capacity: "",
    status: "AVAILABLE", // <-- por defecto
    containerTypeId: "",
    ...initialValues,
  });
  const [errors, setErrors] = useState({});

  const setField = (field, value) => {
    setForm((prev) => ({ ...prev, [field]: value }));
  };

  const validate = () => {
    const newErrors = {};
    if (!form.serial) newErrors.serial = "El serial es requerido";
    if (!form.latitude) newErrors.latitude = "La latitud es requerida";
    if (!form.longitude) newErrors.longitude = "La longitud es requerida";
    if (!form.capacity) newErrors.capacity = "La capacidad es requerida";
    if (!form.containerTypeId) newErrors.containerTypeId = "El tipo es requerido";
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (validate() && onSubmit) {
      onSubmit({
        ...form,
        status: "AVAILABLE",
        latitude: parseFloat(form.latitude),
        longitude: parseFloat(form.longitude),
        capacity: parseFloat(form.capacity),
        containerTypeId: parseInt(form.containerTypeId, 10),
      });
    }
  };

  return { form, errors, setField, handleSubmit };
}