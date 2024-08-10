using Entity.Dto;
using Entity.Model.Parameter;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Data.Interfaces.Operational
{
    public interface IFumigationSuppliesData
    {
        Task Delete(int id);
        Task<IEnumerable<DataSelectDto>> GetAllSelect();
        Task<FumigationSupplies> GetById(int id);
        Task<FumigationSupplies> Save(FumigationSupplies entity);
        Task Update(FumigationSupplies entity);
        Task<IEnumerable<FumigationSupplies>> GetAll();
    }
}
