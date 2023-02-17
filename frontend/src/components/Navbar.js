import { Flex, HStack, Text } from '@chakra-ui/react';
import React from 'react';
import { Link } from 'react-router-dom';
import { ColorModeSwitcher } from './ColorModeSwitcher';

const MenuItem = ({ children, to = '/', ...rest }) => {
  return (
    <Link to={to}>
      <Text
        display="block"
        color="#fff"
        fontWeight="600"
        _hover={{ color: 'teal.600' }}
        {...rest}
      >
        {children}
      </Text>
    </Link>
  );
};

function Navbar() {
  return (
    <Flex align="center" justify="space-between" p="0.75em 4%" bg="#00BCEB">
      <HStack gap="1.5em">
        <Link to="/">
          <Text fontSize="1.5em" fontWeight="bold" color="#fff" pr="1em">
            Log Generator
          </Text>
        </Link>
        <MenuItem to="/">Home</MenuItem>
        <MenuItem to="/jobs">Jobs</MenuItem>
      </HStack>

      <ColorModeSwitcher />
    </Flex>
  );
}

export default Navbar;
