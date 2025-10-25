import { 
  Box, 
  Text, 
  Grid,
  GridItem,
  Button,
  HStack,
  Icon,
  VStack,
  Stack,
  Heading
} from "@chakra-ui/react";
import { MdBusiness, MdLocationOn, MdColorLens, MdImage } from "react-icons/md";
import { useState } from "react";
import UniversitySettingDialog from "../components/UniversitySettingDialog";
import UbicationSettingDialog from "../components/UbicationSettingDialog";
import ReportSettingDialog from "../components/ReportSettingDialog";
import LogoSettingDialog from "../components/LogoSettingDialog";


export default function SettingsPage() {
  const [activeForm, setActiveForm] = useState('institutional');

  const menuItems = [
    {
      id: 'institutional',
      title: 'Información Institucional',
      icon: MdBusiness,
      color: 'blue',
      description: 'Datos de la institución'
    },
    {
      id: 'location',
      title: 'Ubicación',
      icon: MdLocationOn,
      color: 'green',
      description: 'Ubicación en el mapa'
    },
    {
      id: 'logo',
      title: 'Logotipo',
      icon: MdImage,
      color: 'purple',
      description: 'Logo de la institución'
    },
    {
      id: 'report-styles',
      title: 'Estilos de Reporte',
      icon: MdColorLens,
      color: 'orange',
      description: 'Colores de reportes'
    }
  ];

  const renderForm = () => {
    switch (activeForm) {
      case 'institutional':
        return <UniversitySettingDialog />;
      case 'location':
        return <UbicationSettingDialog />;
      case 'logo':
        return <LogoSettingDialog />;
      case 'report-styles':
        return <ReportSettingDialog />;
      default:
        return (
          <VStack spacing={4} align="center" py={20}>
            <Text fontSize="lg" color="gray.500">
              Selecciona una opción para comenzar
            </Text>
          </VStack>
        );
    }
  };

  return (
    <Box p={{ base: 4, md: 8 }} bg="white" minH="100vh">
      <Heading fontSize="3xl" fontWeight="bold" mb={8} color="green.600">
        Configuración Institucional
      </Heading>
      
      <Grid templateColumns={{ base: "1fr", lg: "300px 1fr" }} gap={8}>
        {/* Menu lateral */}
        <GridItem>
          <VStack spacing={4} align="stretch">
            {menuItems.map((item) => (
              <Button
                key={item.id}
                variant={activeForm === item.id ? "solid" : "outline"}
                colorScheme={activeForm === item.id ? "green" : item.color}
                bg={activeForm === item.id ? "green.600" : "teal.700"}
                color={activeForm === item.id ? "white" : "inherit"}
                size="lg"
                h="auto"
                p={4}
                onClick={() => setActiveForm(item.id)}
                justifyContent="flex-start"
                textAlign="left"
                _hover={{
                  bg: activeForm === item.id ? "green.600" : "green.600",
                  color: activeForm === item.id ? "white" : "white"
                }}
              >
                <HStack spacing={3} w="full">
                  <Icon as={item.icon} boxSize={6} />
                  <VStack align="start" spacing={1} flex={1}>
                    <Text fontWeight="semibold" fontSize="md">
                      {item.title}
                    </Text>
                    <Text fontSize="sm" opacity={0.8}>
                      {item.description}
                    </Text>
                  </VStack>
                </HStack>
              </Button>
            ))}
          </VStack>
        </GridItem>

        {/* Contenido del formulario */}
        <GridItem>
          <Box
            bg="gray.50"
            borderRadius="lg"
            p={6}
            h="80vh"
            overflowY="auto"
            border="1px solid"
            borderColor="gray.200"
          >
            {renderForm()}
          </Box>
        </GridItem>
      </Grid>
    </Box>
  );
}
