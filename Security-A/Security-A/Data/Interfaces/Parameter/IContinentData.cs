using Entity.Dto;
using Entity.Model.Parameter;

namespace Data.Interfaces.Parameter
{
    public interface IContinentData
    {
        Task Delete(int id);
        Task<IEnumerable<DataSelectDto>> GetAllSelect();
        Task<Continent> GetById(int id);
        Task<Continent> Save(Continent entity);
        Task Update(Continent entity);
        Task<IEnumerable<Continent>> GetAll();
    }
}
