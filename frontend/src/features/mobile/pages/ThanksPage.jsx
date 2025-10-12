import { Center, Stack, Heading, Text } from "@chakra-ui/react";
import { MdCheckCircle } from "react-icons/md";

export default function ThanksPage() {
  return (
    <Center minH="100vh" bg="#e6f4ea">
      <Stack align="center" spacing={6} bg="white" p={8} rounded="md" boxShadow="md">
        <MdCheckCircle size={80} color="#38A169" />
        <Heading color="#38A169" size="lg" textAlign="center">
          ¡Muchas gracias!
        </Heading>
        <Text fontSize="xl" color="gray.700" textAlign="center">
          Tu reporte fue enviado exitosamente.
        </Text>
      </Stack>
    </Center>
  );
}