import { Flex, Box } from "@chakra-ui/react";
import Sidebar from "../components/SideBar";
import { Outlet } from "react-router-dom";

/**
 * Layout principal con sidebar y área de contenido scrollable.
 */
export default function MainLayout({ children }) {
  return (
    <Flex h="100vh" overflow="hidden">
      <Sidebar />
      <Box
        as="main"
        flex="1"
        minW={0}              // evita que el contenido fuerce overflow horizontal
        overflowY="auto"      // solo scroll vertical
        overflowX="hidden"
        bg="gray.900"         // ajusta según tu tema (o 'gray.50' si usas claro)
      >
        {children || <Outlet />}
      </Box>
    </Flex>
  );
}