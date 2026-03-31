package club.sportsapp.service;

import club.sportsapp.core.exceptions.EntityAlreadyExistsException;
import club.sportsapp.core.exceptions.EntityInvalidArgumentException;
import club.sportsapp.core.exceptions.EntityNotFoundException;
import club.sportsapp.dto.UserInsertDTO;
import club.sportsapp.dto.UserReadOnlyDTO;

import java.util.UUID;

public interface IUserService {

    UserReadOnlyDTO saveUser(UserInsertDTO insertDTO)
        throws EntityAlreadyExistsException, EntityInvalidArgumentException;

    UserReadOnlyDTO getUserByUuid(UUID uuid) throws EntityNotFoundException;
    UserReadOnlyDTO getUserByUuidDeletedFalse(UUID uuid) throws EntityNotFoundException;

    boolean isUserExists(String username);

}
