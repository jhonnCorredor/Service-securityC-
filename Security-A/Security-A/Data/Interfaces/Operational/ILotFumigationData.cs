using Entity.Dto;
using Entity.Model.Security;

namespace Data.Interfaces.Operational
{
    public interface ILotFumigationData
    {
        Task Delete(int id);
        Task<IEnumerable<DataSelectDto>> GetAllSelect();
        Task<LotFumigation> GetById(int id);
        Task<LotFumigation> Save(LotFumigation entity);
        Task Update(LotFumigation entity);
        Task<IEnumerable<LotFumigation>> GetAll();
    }
}
