package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface ItemJpaRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerIdOrderById(Long ownerId, Pageable pageable);

    @Query(value = "select i " +
            "from Item i " +
            "where (upper(i.name)) like upper(concat('%', :text, '%')) " +
            "or upper(i.description) like upper(concat('%', :text, '%')) " +
            "and i.available is true")
    List<Item> findAllByText(@Param("text") String text, Pageable pageable);

    List<Item> findAllByRequestId(long requestId);

    @Query("select i " +
            "from Item i " +
            "where i.request.id is not null "
    )
    List<Item> findAllWithRequestId();
}
