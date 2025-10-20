import { Box, Flex } from "@chakra-ui/react"

export function DashboardCard({
  title,
  right,
  children,
  headerBg = "#1f6d57",
  bodyBg = "#247158",
  titleColor = "#f1f5f9",
}) {
  return (
    <Box w="full" borderRadius="16px" overflow="hidden" boxShadow="sm">
      <Flex
        as="header"
        align="center"
        justify="space-between"
        px={4}
        py={3}
        bg={headerBg}
      >
        <Box as="h3" fontWeight="700" fontSize="18px" color={titleColor}>
          {title}
        </Box>
        {right}
      </Flex>

      <Box bg={bodyBg} px={4} py={3}>
        {children}
      </Box>
    </Box>
  )
}