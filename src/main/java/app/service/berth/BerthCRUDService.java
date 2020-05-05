package app.service.berth;

import app.config.exception.impl.NotFoundException;
import app.config.security.OperationContext;
import app.database.entity.Account;
import app.database.entity.Berth;
import app.database.entity.DictAmenity;
import app.database.repository.BerthRepository;
import app.service.PermissionService;
import app.service.SoftDeleteService;
import app.service.berth.dto.BerthDto;
import app.service.berth.dto.BerthPlaceDto;
import app.service.berth.dto.DictAmenityDto;
import app.service.converters.impl.BerthConverter;
import app.service.converters.impl.BerthPlaceConverter;
import app.service.converters.impl.DictAmenityConverter;
import lombok.RequiredArgsConstructor;
import one.util.streamex.StreamEx;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BerthCRUDService {

    private final EntityManager em;
    private final BerthConverter berthConverter;
    private final BerthRepository berthRepository;
    private final PermissionService permissionService;
    private final SoftDeleteService softDeleteService;
    private final BerthPlaceConverter berthPlaceConverter;
    private final DictAmenityConverter dictAmenityConverter;

    @Transactional(readOnly = true)
    public BerthDto.Resp get(long id, BerthPart... include) {
        Berth berth = berthRepository.findById(id).orElseThrow(NotFoundException::new);
        return berthConverter.toDto(berth, include);
    }

    @Transactional(readOnly = true)
    public List<BerthDto.Resp> get(BerthPart... include) {
        List<Berth> berths = berthRepository.findAllByOwner(em.getReference(Account.class, OperationContext.accountId()));
        berths = StreamEx.of(berths).sortedBy(Berth::getId).reverseSorted().toList();
        return berthConverter.toDtos(berths, include);
    }

    public Long create(BerthDto dto) {
        var berth = new Berth()
                .setOwner(em.getReference(Account.class, OperationContext.accountId()))
                .setIsConfirmed(false);

        berthConverter.toEntity(berth, dto);
        return berthRepository.save(berth).getId();
    }

    @Transactional
    public void update(long id, BerthDto dto) {
        Berth berth = berthRepository.findById(id).orElseThrow(NotFoundException::new);
        permissionService.check(berth);

        dto.setLat(berth.getLat());
        dto.setLng(berth.getLng());

        berthConverter.toEntity(berth, dto);
    }

    @Transactional
    public void delete(long id) {
        Berth berth = berthRepository.findById(id).orElseThrow(NotFoundException::new);
        permissionService.check(berth);

        softDeleteService.delete(berth);
        softDeleteService.delete(berth.getBerthPlaces());
    }

    @Transactional
    public List<BerthPlaceDto> getPlaces(long id) {
        Berth berth = berthRepository.findById(id).orElseThrow(NotFoundException::new);
        return berthPlaceConverter.toDtos(berth.getBerthPlaces());
    }

    @Transactional
    public List<DictAmenityDto> getAmenities(long id) {
        Berth berth = berthRepository.findById(id).orElseThrow(NotFoundException::new);
        List<DictAmenity> amenities = StreamEx.of(berth.getAmenities()).sortedBy(DictAmenity::getKey).toList();
        return dictAmenityConverter.toDtos(amenities);
    }
}
