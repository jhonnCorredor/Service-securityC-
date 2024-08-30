using Entity.Dto;
using Entity.Dto.Operational;
using Entity.Model.Operational;

namespace Business.Interfaces.Operational
{
    public interface IFumigationBusiness
    {
        Task Delete(int id);
        Task<IEnumerable<DataSelectDto>> GetAllSelect();
        Task<FumigationDto> GetById(int id);
        Task<Fumigation> Save(FumigationDto entity);
        Task Update(FumigationDto entity);
        Fumigation mapearDatos(Fumigation fumigation, FumigationDto entity);
        Task<IEnumerable<FumigationDto>> GetAll();
    }
}
