package gr.aegean.icsd.autorepair.shared.security.service;

import gr.aegean.icsd.autorepair.shared.security.CustomUserDetails;
import gr.aegean.icsd.autorepair.user.User;
import gr.aegean.icsd.autorepair.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(()->new UsernameNotFoundException("user not found"));
        return new CustomUserDetails(user);
    }
}
