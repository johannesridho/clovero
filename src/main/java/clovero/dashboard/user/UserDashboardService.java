package clovero.dashboard.user;

import clovero.datatables.DatatablesCriteria;
import clovero.datatables.DatatablesResponse;
import clovero.user.User;
import clovero.user.UserRepository;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import static lombok.AccessLevel.PRIVATE;

@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
@AllArgsConstructor
public class UserDashboardService {

    UserRepository userRepository;

    public DatatablesResponse findWithDatatablesCriteria(DatatablesCriteria criteria) {
        final Page<User> users = userRepository.findAll(
                new UserDatatablesSpecification(criteria),
                criteria.getPageRequest());

        return new DatatablesResponse(
                users.getContent(),
                users.getTotalElements(),
                userRepository.count(),
                criteria.getDraw());
    }

}
