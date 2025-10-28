import {
  Box,
  Heading,
  Stack,
  Button,
  Text,
  VStack,
  HStack,
  Image,
  Input,
} from "@chakra-ui/react";
import { useState, useRef, useEffect } from "react";
import { SettingsAPI } from "../api/api.settings";
import { toast } from "react-hot-toast";

export default function LogoSettingDialog() {
  const [logoFile, setLogoFile] = useState(null);
  const [logoPreview, setLogoPreview] = useState(null);
  const [currentLogo, setCurrentLogo] = useState(null);
  const [loading, setLoading] = useState(false);
  const [loadingData, setLoadingData] = useState(true);
  const fileInputRef = useRef(null);

  useEffect(() => {
    loadCurrentLogo();
  }, []);

  const loadCurrentLogo = async () => {
    setLoadingData(true);
    try {
      const response = await SettingsAPI.loadLogo();
      const blobURL = URL.createObjectURL(response.data);
      setCurrentLogo(blobURL);      
    } catch (error) {
      console.error("Error loading current logo:", error);
      // No mostrar error al usuario, simplemente no hay logo actual
    } finally {
      setLoadingData(false);
    }
  };

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setLogoFile(file);
      
      // Crear preview
      const reader = new FileReader();
      reader.onload = (e) => {
        setLogoPreview(e.target.result);
      };
      reader.readAsDataURL(file);
    }
  };

  const handleUpload = async () => {
    if (!logoFile) {
      toast.error("Por favor selecciona un archivo");
      return;
    }

    setLoading(true);
    try {
      await SettingsAPI.uploadLogo(logoFile);
      toast.success("Logo actualizado correctamente");
      setLogoFile(null);
      setLogoPreview(null);
      if (fileInputRef.current) {
        fileInputRef.current.value = "";
      }
      // Recargar el logo actual después de subir
      await loadCurrentLogo();
    } catch (error) {
      toast.error("Error al actualizar el logo");
    } finally {
      setLoading(false);
    }
  };

  const handleRemove = () => {
    setLogoFile(null);
    setLogoPreview(null);
    if (fileInputRef.current) {
      fileInputRef.current.value = "";
    }
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
          Logotipo Institucional
        </Text>
      </Box>

      {/* Contenido del formulario */}
      <Box px={6} py={6}>
        <Stack spacing={6}>
        {/* Preview del logo actual */}
        <VStack align="start" spacing={2}>
          <Text fontSize="sm" fontWeight="medium" color="gray.700">
            Logo actual
          </Text>
          <Box
            border="2px dashed"
            borderColor="gray.300"
            borderRadius="md"
            p={4}
            w="200px"
            h="120px"
            display="flex"
            alignItems="center"
            justifyContent="center"
            bg="gray.50"
          >
            {loadingData ? (
              <Text fontSize="sm" color="gray.500">
                Cargando...
              </Text>
            ) : currentLogo ? (
              <Image
                src={currentLogo}
                alt="Logo actual"
                maxH="100px"
                maxW="180px"
                objectFit="contain"
              />
            ) : (
              <Text fontSize="sm" color="gray.500">
                No hay logo configurado
              </Text>
            )}
          </Box>
        </VStack>

        {/* Selector de archivo */}
        <VStack align="start" spacing={2}>
          <Text fontSize="sm" fontWeight="medium" color="gray.700">
            Seleccionar nuevo logo
          </Text>
          <Input
            ref={fileInputRef}
            type="file"
            accept="image/*"
            onChange={handleFileChange}
            display="none"
          />
          <Button
            onClick={() => fileInputRef.current?.click()}
            variant="outline"
            colorScheme="teal"
            size="md"
          >
            Seleccionar archivo
          </Button>
          <Text fontSize="xs" color="gray.500">
            Formatos soportados: JPG, PNG, GIF. Tamaño máximo: 5MB
          </Text>
        </VStack>

        {/* Preview del nuevo logo */}
        {logoPreview && (
          <VStack align="start" spacing={2}>
            <Text fontSize="sm" fontWeight="medium" color="gray.700">
              Vista previa
            </Text>
            <Box
              border="1px solid"
              borderColor="gray.300"
              borderRadius="md"
              p={4}
              bg="white"
            >
              <Image
                src={logoPreview}
                alt="Preview del logo"
                maxH="120px"
                maxW="200px"
                objectFit="contain"
              />
            </Box>
            <HStack spacing={2}>
              <Button
                size="sm"
                colorScheme="red"
                variant="outline"
                onClick={handleRemove}
              >
                Cancelar
              </Button>
              <Button
                size="sm"
                colorScheme="teal"
                onClick={handleUpload}
                isLoading={loading}
                loadingText="Subiendo..."
              >
                Subir logo
              </Button>
            </HStack>
          </VStack>
        )}

        {/* Información adicional */}
        <VStack align="start" spacing={2} p={4} bg="blue.50" borderRadius="md">
          <Text fontSize="sm" fontWeight="medium" color="blue.700">
            Recomendaciones:
          </Text>
          <Text fontSize="xs" color="blue.600">
            • Usa imágenes de alta calidad (mínimo 200x200px)
          </Text>
          <Text fontSize="xs" color="blue.600">
            • Formatos recomendados: PNG con fondo transparente
          </Text>
          <Text fontSize="xs" color="blue.600">
            • El logo aparecerá en reportes y documentos oficiales
          </Text>
        </VStack>
        </Stack>
      </Box>
    </Stack>
  );
}
