import {
  Box,
  Heading,
  Stack,
  Input,
  Button,
  Text,
  VStack,
  HStack
} from "@chakra-ui/react";
import { useState, useEffect } from "react";
import { SettingsAPI } from "../api/api.settings";
import { toast } from "react-hot-toast";

export default function UniversitySettingDialog() {
  const [form, setForm] = useState({
    legalName: "",
    taxId: { type: "", number: "" },
    address1: "",
    address2: "",
    address3: "",
    phone: "",
    email: "",
    logoPath: "",
  });

  useEffect(() => {
    SettingsAPI.getUniversity()
      .then((res) => {
        const data = res.data.data || {};
        setForm({
          legalName: data.legalName || "",
          taxId: {
            type: data.taxId?.type || "",
            number: data.taxId?.number || ""
          },
          address1: data.address1 || "",
          address2: data.address2 || "",
          address3: data.address3 || "",
          phone: data.phone || "",
          email: data.email || "",
          logoPath: data.logoPath || "",
        });
      })
      .catch((error) => {
        console.error("Error loading university data:", error);
        toast.error("Error al cargar los datos institucionales");
      });
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    if (name.startsWith("taxId.")) {
      const key = name.split(".")[1];
      setForm((prev) => ({
        ...prev,
        taxId: { ...prev.taxId, [key]: value },
      }));
    } else {
      setForm((prev) => ({ ...prev, [name]: value }));
    }
  };

  const handleSubmit = async () => {
    try {
      await SettingsAPI.updateUniversity(form);
      toast.success("Datos institucionales actualizados.");
    } catch {
      toast.error("Error al actualizar los datos.");
    }
  };

  const renderField = (name, label, value, placeholder, required = false) => (
    <VStack align="start" spacing={1} w="full">
      <Text fontSize="sm" fontWeight="medium" color="gray.700">
        {label}
        {required && <Text as="span" color="red.500" ml={1}>*</Text>}
      </Text>
      <Input
        name={name}
        value={value}
        onChange={handleChange}
        placeholder={placeholder}
        size="md"
        w="full"
        bg="white"
        color="teal.700"
        borderColor="gray.300"
        pl={2}
        _hover={{ borderColor: "green.400" }}
        _focus={{ 
          borderColor: "green.600", 
          boxShadow: "0 0 0 1px var(--chakra-colors-green-600)",
          bg: "white"
        }}
        _placeholder={{ color: "gray.500" }}
      />
      <Text fontSize="xs" color="gray.500">
        {required ? "Este campo es obligatorio" : "Opcional"}
      </Text>
    </VStack>
  );

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
    >
      {/* Encabezado verde */}
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
          Datos Institucionales
        </Text>
      </Box>

      {/* Contenido del formulario */}
      <Box px={6} py={6}>
        <Stack spacing={4}>
        {renderField("legalName", "Nombre legal", form.legalName || "", "Nombre legal", true)}
        {renderField("taxId.type", "Tipo de RIF", form.taxId?.type || "", "Tipo de RIF", true)}
        {renderField("taxId.number", "Número de RIF", form.taxId?.number || "", "Número de RIF", true)}
        {renderField("address1", "Dirección 1", form.address1 || "", "Dirección 1")}
        {renderField("address2", "Dirección 2", form.address2 || "", "Dirección 2")}
        {renderField("address3", "Dirección 3", form.address3 || "", "Dirección 3")}
        {renderField("phone", "Teléfono", form.phone || "", "Teléfono")}
        {renderField("email", "Correo electrónico", form.email || "", "Correo electrónico")}
        <Button 
          colorScheme="green" 
          bg="green.600"
          color="white"
          _hover={{ bg: "green.700" }}
          _active={{ bg: "green.800" }}
          boxShadow="sm"
          onClick={handleSubmit}
        >
          Guardar
        </Button>
        </Stack>
      </Box>
    </Stack>
  );
}
