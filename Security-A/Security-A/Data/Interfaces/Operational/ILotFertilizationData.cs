using Entity.Dto;
using Entity.Model.Security;

namespace Data.Interfaces.Operational
{
    public interface ILotFertilizationData
    {
        Task Delete(int id);
        Task<IEnumerable<DataSelectDto>> GetAllSelect();
        Task<LotFertilization> GetById(int id);
        Task<LotFertilization> Save(LotFertilization entity);
        Task Update(LotFertilization entity);
        Task<IEnumerable<LotFertilization>> GetAll();
    }
}
