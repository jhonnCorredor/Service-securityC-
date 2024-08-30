using Entity.Dto;
using Entity.Dto.Operational;
using Entity.Model.Security;

namespace Business.Interfaces.Operational
{
    public interface ILotFertilizationBusiness
    {
        Task Delete(int id);
        Task<IEnumerable<DataSelectDto>> GetAllSelect();
        Task<LotFertilizationDto> GetById(int id);
        Task<LotFertilization> Save(LotFertilizationDto entity);
        Task Update(LotFertilizationDto entity);
        LotFertilization mapearDatos(LotFertilization LotFertilization, LotFertilizationDto entity);
        Task<IEnumerable<LotFertilizationDto>> GetAll();
    }
}
