import { Flex, Box } from "@chakra-ui/react";
import Sidebar from "../components/SideBar";
import { Outlet } from "react-router-dom";
import { UserProfileBubble } from "@/components/UserProfileBubble";
import { useContext } from "react";
import AuthContext  from "../context/AuthContext";
import { SessionExpiredDialog } from "../components/SessionExpiredDialog";

/**
 * Layout principal con sidebar y área de contenido scrollable.
 */
export default function MainLayout({ children }) {
  const { sessionExpired } = useContext(AuthContext);

  return (
    <Flex direction="column" h="100vh" overflow="hidden">
      {/* Header transparente, solo para la burbuja */}
      <Flex
        as="header"
        position="fixed"      // <-- Fijo en la pantalla
        top={0}
        right={0}
        zIndex={20}
        bg="transparent"
        boxShadow="none"
        w="auto"
        h="auto"
        p={0}
        pointerEvents="none"
      >
        <Box pointerEvents="auto" mr={6} mt={4}>
          <UserProfileBubble />
        </Box>
      </Flex>
      <Flex flex="1">
        <Sidebar />
        <Box
          as="main"
          flex="1"
          minW={0}
          overflowY="auto"
          overflowX="hidden"
          bg="gray.900"
        >
          {children || <Outlet />}
        </Box>
      </Flex>
      <SessionExpiredDialog isOpen={sessionExpired} />
    </Flex>
  );
}