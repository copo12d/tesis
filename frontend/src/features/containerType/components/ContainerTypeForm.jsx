import React from "react";
import {
  Stack,
  Button,
  Text,
  Box,
} from "@chakra-ui/react";
import { LiaTagSolid, LiaInfoCircleSolid } from "react-icons/lia";
import { useContainerTypeForm } from "../hooks/useContainerTypeform";
import { IconInputField } from "@/components/ui/IconInputField";
import { toast } from "react-hot-toast";

export function ContainerTypeForm({
  initialValues = { name: "", description: "" },
  loading = false,
  onSubmit,
  submitText = "Guardar",
  title,
}) {
  const {
    form,
    errors,
    setField,
    handleSubmit,
    validate,
  } = useContainerTypeForm({ initialValues, onSubmit });

  const iconAddonProps = { bg: "teal.700", px: 3 };
  const isEdit = !!initialValues?.id;

  const handleFieldChange = (name, value) => {
    setField(name, value);
  };

  const handleValidatedSubmit = (e) => {
    e.preventDefault();
    const validationErrors = validate(form);
    if (Object.keys(validationErrors).length > 0) {
      toast.error("Debes llenar todos los campos");
      return;
    }
    onSubmit(form);
  };

  return (
    <Stack
      spacing={0}
      borderRadius="md"
      boxShadow="md"
      borderWidth={1}
      borderColor="green.600"
      bg="whiteAlpha.900"
      maxW="6xl"
      mx="auto"
      mt={6}
    >
      {/* Encabezado */}
      <Box
        bg="green.600"
        color="white"
        px={6}
        py={4}
        borderTopRadius="md"
        borderBottom="1px solid"
        borderColor="green.700"
      >
        <Text fontSize="xl" fontWeight="bold">
          {title || (isEdit ? "Editar tipo de contenedor" : "Registrar tipo de contenedor")}
        </Text>
      </Box>

      {/* Formulario */}
      <Box px={6} py={6}>
        <form onSubmit={handleValidatedSubmit}>
          <Stack spacing={6}>
            <IconInputField
              label="Nombre"
              name="name"
              value={form.name}
              onChange={(e) => handleFieldChange("name", e.target.value)}
              placeholder="Ej: Papel, Plástico, Orgánicos..."
              icon={<LiaTagSolid />}
              iconProps={iconAddonProps}
              type="text"
              required
              disabled={loading}
              error={errors.name}
              inputProps={{
                autoComplete: "name",
                w: "100%",
                pl: 2,
                _placeholder: { pl: 2 },
              }}
            />

            <IconInputField
              label="Descripción"
              name="description"
              value={form.description}
              onChange={(e) => handleFieldChange("description", e.target.value)}
              placeholder="Describe el tipo de desecho"
              icon={<LiaInfoCircleSolid />}
              iconProps={iconAddonProps}
              type="text"
              required
              disabled={loading}
              error={errors.description}
              inputProps={{
                autoComplete: "off",
                w: "100%",
                pl: 2,
                _placeholder: { pl: 2 },
              }}
            />

            <Button
              type="submit"
              colorPalette="green"
              size="lg"
              isLoading={loading}
              loadingText="Guardando..."
              spinnerPlacement="end"
              alignSelf="flex-end"
              px={2}
            >
              {submitText}
            </Button>
          </Stack>
        </form>
      </Box>
    </Stack>
  );
}
