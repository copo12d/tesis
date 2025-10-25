import {
  Box,
  Heading,
  Stack,
  Button,
  Text,
  VStack,
  HStack,
  Input,
} from "@chakra-ui/react";
import { useState, useEffect } from "react";
import { SettingsAPI } from "../api/api.settings";
import { toast } from "react-hot-toast";

export default function ReportSettingDialog() {
  const [form, setForm] = useState({
    tableHeaderColor: "#0000FF",
    headerTextColor: "#FFFFFF", 
    recordColor: "#000000",
  });
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    SettingsAPI.getReport()
      .then((res) => {
        const data = res.data.data || {};
        setForm({
          tableHeaderColor: data.tableHeaderColor,
          headerTextColor: data.headerTextColor,
          recordColor: data.recordColor,
        });
      })
      .catch((error) => {
        console.error("Error loading report data:", error);
        toast.error("Error al cargar los datos de estilos");
      });
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    const fieldName = name.replace("_picker", "");
    setForm((prev) => ({ ...prev, [fieldName]: value }));
  };

  const handleSubmit = async () => {
    setLoading(true);
    try {
      await SettingsAPI.updateReport(form);
      toast.success("Estilos de reporte actualizados.");
    } catch {
      toast.error("Error al actualizar los estilos.");
    } finally {
      setLoading(false);
    }
  };

  const renderColorField = (name, label, value, required = false) => (
    <VStack align="start" spacing={1} w="full" key={name}>
      <Text fontSize="sm" fontWeight="medium" color="gray.700">
        {label}
        {required && <Text as="span" color="red.500" ml={1}>*</Text>}
      </Text>
      <HStack spacing={2} w="full" position="relative">
        <Input
          name={name}
          value={value}
          onChange={handleChange}
          placeholder="Ej. #FF0000"
          size="md"
          w="full"
          bg="white"
          color="teal.700"
          borderColor="gray.300"
          pl={2}
          _hover={{ borderColor: "teal.700" }}
          _focus={{ 
            borderColor: "green.600", 
            boxShadow: "0 0 0 1px var(--chakra-colors-green-600)",
            bg: "white"
          }}
          _placeholder={{ color: "gray.500" }}
        />
        <Box
          w="40px"
          h="40px"
          bg={value}
          border="2px solid"
          borderColor="gray.300"
          borderRadius="md"
          cursor="pointer"
          onClick={() => {
            const input = document.querySelector(`input[name="${name}_picker"]`);
            if (input) input.click();
          }}
        />
        <Input
          type="color"
          name={`${name}_picker`}
          value={value}
          onChange={handleChange}
          w="0"
          h="0"
          opacity="0"
          position="absolute"
          pointerEvents="none"
        />
      </HStack>
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
          Estilos de Reporte
        </Text>
      </Box>

      {/* Contenido del formulario */}
      <Box px={6} py={6}>
        <Stack spacing={4}>
          {renderColorField("tableHeaderColor", "Color de encabezado", form.tableHeaderColor, true)}
          {renderColorField("headerTextColor", "Color de texto del encabezado", form.headerTextColor, true)}
          {renderColorField("recordColor", "Color de registros", form.recordColor, true)}
          <Button 
            colorScheme="green" 
            bg="green.600"
            color="white"
            _hover={{ bg: "green.700" }}
            _active={{ bg: "green.800" }}
            boxShadow="sm"
            onClick={handleSubmit} 
            isLoading={loading} 
            loadingText="Guardando..."
          >
            Guardar estilos
          </Button>
        </Stack>
      </Box>
    </Stack>
  );
}
