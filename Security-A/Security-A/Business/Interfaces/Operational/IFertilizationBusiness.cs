using Entity.Dto;
using Entity.Dto.Operational;
using Entity.Model.Operational;

namespace Business.Interfaces.Operational
{
    public interface IFertilizationBusiness
    {
        Task Delete(int id);
        Task<IEnumerable<DataSelectDto>> GetAllSelect();
        Task<FertilizationDto> GetById(int id);
        Task<Fertilization> Save(FertilizationDto entity);
        Task Update(FertilizationDto entity);
        Fertilization mapearDatos(Fertilization fertilization, FertilizationDto entity);
        Task<IEnumerable<FertilizationDto>> GetAll();
    }
}
