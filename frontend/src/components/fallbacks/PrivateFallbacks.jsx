import { Box, Flex, Stack, Skeleton, SkeletonText } from "@chakra-ui/react";

export function PrivateAppFallback() {
  return (
    <Box w="100vw" h="100svh" bg="#e6f4ea">
      <Flex w="100%" h="100%">
        {/* Sidebar skeleton (solo desktop) */}
        <Box
          as="aside"
          display={{ base: "none", md: "block" }}
          w={{ md: "260px" }}
          bg="white"
          boxShadow="sm"
          p={4}
        >
          <Skeleton height="24px" w="70%" mb={6} />
          <Stack spacing={3}>
            <Skeleton height="10px" />
            <Skeleton height="10px" />
            <Skeleton height="10px" />
            <Skeleton height="10px" />
            <Skeleton height="10px" />
          </Stack>
        </Box>

        {/* Área principal skeleton */}
        <Box flex="1" p={{ base: 4, md: 6 }}>
          <Skeleton height="28px" w="30%" mb={4} />
          <Stack spacing={4}>
            <Flex gap={4} direction={{ base: "column", md: "row" }}>
              <Skeleton height="96px" flex="1" />
              <Skeleton height="96px" flex="1" />
              <Skeleton height="96px" flex="1" />
            </Flex>
            <Skeleton height="280px" />
            <SkeletonText noOfLines={3} spacing="3" />
          </Stack>
        </Box>
      </Flex>
    </Box>
  );
}

export function PageContentSkeleton() {
  return (
    <Box p={{ base: 4, md: 6 }}>
      <Stack spacing={4}>
        <Flex gap={4} direction={{ base: "column", md: "row" }}>
          <Skeleton height="96px" flex="1" />
          <Skeleton height="96px" flex="1" />
          <Skeleton height="96px" flex="1" />
        </Flex>
        <Skeleton height="320px" />
      </Stack>
    </Box>
  );
}