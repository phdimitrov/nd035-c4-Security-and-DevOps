package com.example.demo.security;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailsServiceImplTest {

    private UserDetailsServiceImpl userDetailsService;

    private UserRepository userRepository = mock(UserRepository.class);

    @Before
    public void setUp() {
        userDetailsService = new UserDetailsServiceImpl();
        TestUtils.injectObjects(userDetailsService, "userRepository", userRepository);
    }

    @Test
    public void load_user() {
        User user = TestUtils.createMockedUser();

        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

        final UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        assertEquals(user.getUsername(), userDetails.getUsername());
    }

    @Test(expected = UsernameNotFoundException.class)
    public void load_user_exception() {
        User user = TestUtils.createMockedUser();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

        final UserDetails userDetails = userDetailsService.loadUserByUsername("unknown");
        assertEquals(user.getUsername(), userDetails.getUsername());
    }

}
