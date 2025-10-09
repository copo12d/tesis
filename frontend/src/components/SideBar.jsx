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
  FiHome,
  FiInfo,
  FiBriefcase,
  FiMail,
  FiLogOut,
  FiGrid,
  FiShield,
} from "react-icons/fi";
import { useContext } from "react";
import AuthContext from "../context/Authcontext";
import AdminSection from "./AdminSection";
import { useNavigate } from "react-router-dom";



const Sidebar = () => {
  const navigate = useNavigate();
  const { user, setSessionExpired } = useContext(AuthContext);

  const navItems = [
    {
      label: "Dashboard",
      icon: FiGrid,
      onClick: () => {
        navigate("/")
      },
    },
    { label: "Contact", icon: FiMail },
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
          Menu
        </Text>
        <VStack spacing={1} align="stretch" flex={1}>
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
          <AdminSection user={user} />
          <Box pt={2} mt={2} borderTop="1px solid rgba(255,255,255,0.12)">
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
              onClick={() => setSessionExpired(true)} // Cambia esto para probar el modal
            >
              <Box as={FiLogOut} boxSize={5} opacity={0.9} />
              Logout
            </Button>
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
