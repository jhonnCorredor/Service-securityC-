using Entity.Dto;
using Entity.Dto.Operational;
using Entity.Model.Security;

namespace Business.Interfaces.Operational
{
    public interface ILotFumigationBusiness
    {
        Task Delete(int id);
        Task<IEnumerable<DataSelectDto>> GetAllSelect();
        Task<LotFumigationDto> GetById(int id);
        Task<LotFumigation> Save(LotFumigationDto entity);
        Task Update(LotFumigationDto entity);
        LotFumigation mapearDatos(LotFumigation LotFumigation, LotFumigationDto entity);
        Task<IEnumerable<LotFumigationDto>> GetAll();
    }
}
