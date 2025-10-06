import { Center, Stack, Heading, Text, Button } from "@chakra-ui/react";
import { useNavigate } from "react-router-dom";
import { MdPhoneIphone } from "react-icons/md";
import { Link } from "react-router-dom";

export default function MobileReportPage() {
  const navigate = useNavigate();

  return (
    <Center h="100vh" bg="#e6f4ea">
      <Stack
        boxShadow="md"
        bg="white"
        p={10}
        rounded="md"
        w={350}
        align="center"
        spacing={8}
      >
        <MdPhoneIphone size={80} color="#009688" />
        <Heading color="#009688" textAlign="center" size="md">
          Esta sección solo está disponible en móvil
        </Heading>
        <Text fontSize="md" color="gray.700" textAlign="center">
          Próximamente podrás reportar el estado del contenedor desde aquí.
        </Text>
        <Text fontSize="sm" color="gray.700" textAlign="center">
          ¿Eres recolector?{" "}
          <Button
            variant="link"
            color="#009688"
            onClick={() => navigate("/mobile/login")}
            p={0}
            h="auto"
            minW={0}
          >
            Inicia sesión aquí
          </Button>
        </Text>
      </Stack>
    </Center>
  );
}