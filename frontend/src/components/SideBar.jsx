import {
  Box,
  Flex,
  Text,
  VStack,
  Button,
  Portal,
  Popover,
} from "@chakra-ui/react";
import {
  FiBarChart2,
  FiSettings,
  FiBriefcase,
  FiMail,
  FiLogOut,
  FiGrid,
  FiShield,
} from "react-icons/fi";
import { useContext } from "react";
import AuthContext from "@/context/AuthContext";
import AdminSection from "./AdminSection";
import { useNavigate } from "react-router-dom";
import { LogoutDialog } from "./LogoutDialog";

const Sidebar = () => {
  const navigate = useNavigate();
  const { user, logout } = useContext(AuthContext); // <-- aquí obtienes logout

  // Configura aquí el archivo y el nombre a descargar (ubicado en public/)
  const MANUAL_URL = "/public/manual de usuario.pdf";
  const MANUAL_FILENAME = "Manual-de-Usuario.pdf";

  const downloadManual = (filename = MANUAL_FILENAME, url = MANUAL_URL) => {
    const a = document.createElement("a");
    a.href = url;
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    a.remove();
  };

  const navItems = [
    // Ayuda: descarga el PDF del manual (opción 2: descarga programática)
    { label: "Ayuda", icon: FiMail, onClick: () => downloadManual() },
    { label: "Configuración", icon: FiSettings, onClick: () => navigate("/settings") },
  ];

  return (
    <Flex h="100vh" overflow="hidden">
      <Box
        w="240px"
        bgGradient="linear(to-b, teal.600, teal.700)"
        bg="teal.700"
        color="white"
        h="100%"
        px={6}
        py={6}
        boxShadow="lg"
        display="flex"
        flexDirection="column"
        borderRight="1px solid rgba(255,255,255,0.15)"
        overflow="hidden"
      >
        <Text fontSize="2xl" fontWeight="bold" mb={6}>
          Menú Principal
        </Text>

        <VStack spacing={1} align="stretch" flex={1}>
          <Button
            key={"Centro de Análisis"}
            variant="ghost"
            justifyContent="flex-start"
            color="white"
            fontWeight="medium"
            _hover={{ bg: "whiteAlpha.200", transform: "translateX(2px)" }}
            _active={{ bg: "whiteAlpha.300" }}
            transition="all 0.15s"
            h="42px"
            px={3}
            borderRadius="md"
            display="flex"
            gap={3}
            onClick={() => navigate("/")}
          >
            <Box as={FiBarChart2} boxSize={5} opacity={0.9} />
            {"Centro de Análisis"}
          </Button>
          <AdminSection user={user} />
          {navItems.map((item) => {
            const Icon = item.icon;
            return (
              <Button
                key={item.label}
                variant="ghost"
                justifyContent="flex-start"
                color="white"
                fontWeight="medium"
                _hover={{ bg: "whiteAlpha.200", transform: "translateX(2px)" }}
                _active={{ bg: "whiteAlpha.300" }}
                transition="all 0.15s"
                h="42px"
                px={3}
                borderRadius="md"
                display="flex"
                gap={3}
                onClick={item.onClick}
              >
                <Box as={Icon} boxSize={5} opacity={0.9} />
                {item.label}
              </Button>
            );
          })}
          <Box pt={2} mt={2} borderTop="1px solid rgba(255,255,255,0.12)">
            <LogoutDialog
              trigger={
                <Button
                  variant="ghost"
                  justifyContent="flex-start"
                  color="white"
                  fontWeight="medium"
                  _hover={{ bg: "whiteAlpha.200", transform: "translateX(2px)" }}
                  _active={{ bg: "whiteAlpha.300" }}
                  transition="all 0.15s"
                  h="42px"
                  px={3}
                  borderRadius="md"
                  display="flex"
                  gap={3}
                >
                  <Box as={FiLogOut} boxSize={5} opacity={0.9} />
                  Cerrar Sesión
                </Button>
              }
              onLogout={logout} // <-- aquí pasas la función de logout
            />
          </Box>
        </VStack>
        <Text fontSize="xs" opacity={0.6} mt={4} mb={1}>
          © {new Date().getFullYear()}
        </Text>
      </Box>
    </Flex>
  );
};

export default Sidebar;