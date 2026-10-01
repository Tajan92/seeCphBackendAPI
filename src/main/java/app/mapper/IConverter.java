package app.mapper;

public interface IConverter<Entity, response, request> {

    public Entity convertDTOToEntity(request dto);

    public response convertEntityToDTO(Entity entity);
}
