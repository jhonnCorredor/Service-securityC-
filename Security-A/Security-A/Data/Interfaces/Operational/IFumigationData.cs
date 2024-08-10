using Entity.Dto;
using Entity.Model.Operational;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Data.Interfaces.Operational
{
    public interface IFumigationData
    {
        Task Delete(int id);
        Task<IEnumerable<DataSelectDto>> GetAllSelect();
        Task<Fumigation> GetById(int id);
        Task<Fumigation> Save(Fumigation entity);
        Task Update(Fumigation entity);
        Task<IEnumerable<Fumigation>> GetAll();
    }
}
