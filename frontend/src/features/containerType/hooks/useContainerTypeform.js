import { useState, useEffect } from "react";

export function useContainerTypeForm({ initialValues = {}, onSubmit }) {
  const [form, setForm] = useState({
    name: initialValues?.name || "",
    description: initialValues?.description || "",
  });
  const [errors, setErrors] = useState({});

  useEffect(() => {
    setForm({
      name: initialValues?.name || "",
      description: initialValues?.description || "",
    });
    setErrors({});
  }, [initialValues]);

  const setField = (field, value) => {
    setForm((prev) => ({ ...prev, [field]: value }));
    setErrors((prev) => ({ ...prev, [field]: "" }));
  };

  const validate = (values = form) => {
    const newErrors = {};
    if (!values.name || values.name.toString().trim() === "") {
      newErrors.name = "El nombre es obligatorio";
    }
    if (!values.description || values.description.toString().trim() === "") {
      newErrors.description = "La descripción es obligatoria";
    }
    setErrors(newErrors);
    return newErrors;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    let newErrors = {};
    if (!form.name) newErrors.name = "El nombre es obligatorio";
    if (!form.description) newErrors.description = "La descripción es obligatoria";
    setErrors(newErrors);
    if (Object.keys(newErrors).length === 0 && onSubmit) {
      await onSubmit(form);
    }
  };

  return {
    form,
    errors,
    setField,
    handleSubmit,
    validate,
  };
}